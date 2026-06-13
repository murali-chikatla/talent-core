package com.nexora.rsp.talentcore.search;

import com.nexora.rsp.talentcore.domain.User;
import com.nexora.rsp.talentcore.domain.UserStatus;
import com.nexora.rsp.talentcore.dto.UserSearchRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenericSearchBuilderTest {

    @Mock
    private AutowireCapableBeanFactory beanFactory;

    @Mock
    private Root<User> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Object> firstNamePath;

    @Mock
    private Path<Object> userStatusPath;

    @Mock
    private Join<Object, Object> userRolesJoin;

    @Mock
    private Join<Object, Object> roleJoin;

    @Mock
    private Path<Object> roleCodePath;

    @Mock
    private Expression<String> firstNameExpression;

    @Mock
    private Expression<String> loweredFirstNameExpression;

    @Mock
    private Predicate firstNamePredicate;

    @Mock
    private Predicate activePredicate;

    @Mock
    private Predicate roleCodePredicate;

    @Mock
    private Predicate combinedPredicate;

    @Test
    void buildCreatesSpecificationsForNonBlankAnnotatedFieldsIncludingNestedPaths() {

        UserSearchRequest request = new UserSearchRequest();
        request.setFirstName(" Murali ");
        request.setLastName(" ");
        request.setEmail(null);
        request.setActive(true);
        request.setRoleCode("TEAM_MANAGER");

        when(beanFactory.createBean(IdentitySearchValueTransformer.class))
                .thenReturn(new IdentitySearchValueTransformer());
        when(beanFactory.createBean(ActiveStatusSearchValueTransformer.class))
                .thenReturn(new ActiveStatusSearchValueTransformer());
        when(root.get("firstName")).thenReturn(firstNamePath);
        when(firstNamePath.as(String.class)).thenReturn(firstNameExpression);
        when(criteriaBuilder.lower(firstNameExpression)).thenReturn(loweredFirstNameExpression);
        when(criteriaBuilder.like(loweredFirstNameExpression, "%murali%"))
                .thenReturn(firstNamePredicate);
        when(root.get("userStatus")).thenReturn(userStatusPath);
        when(criteriaBuilder.equal(userStatusPath, UserStatus.ACTIVE))
                .thenReturn(activePredicate);
        when(root.join("userRoles", jakarta.persistence.criteria.JoinType.LEFT))
                .thenReturn(userRolesJoin);
        when(userRolesJoin.join("role", jakarta.persistence.criteria.JoinType.LEFT))
                .thenReturn(roleJoin);
        when(roleJoin.get("roleCode")).thenReturn(roleCodePath);
        when(criteriaBuilder.equal(roleCodePath, "TEAM_MANAGER"))
                .thenReturn(roleCodePredicate);
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class)))
                .thenReturn(combinedPredicate);

        Specification<User> specification = new GenericSearchBuilder(beanFactory).build(request);

        Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);

        assertThat(predicate).isSameAs(combinedPredicate);
        verify(query, times(3)).distinct(true);
        verify(criteriaBuilder).like(loweredFirstNameExpression, "%murali%");
        verify(criteriaBuilder).equal(userStatusPath, UserStatus.ACTIVE);
        verify(criteriaBuilder).equal(roleCodePath, "TEAM_MANAGER");
    }

    @Test
    void buildReturnsConjunctionWhenNoSearchFieldHasValue() {

        UserSearchRequest request = new UserSearchRequest();
        request.setFirstName(" ");

        when(criteriaBuilder.conjunction()).thenReturn(combinedPredicate);

        Specification<User> specification = new GenericSearchBuilder(beanFactory).build(request);

        Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);

        assertThat(predicate).isSameAs(combinedPredicate);
    }

    @Test
    void buildUsesDefaultFieldNameWhenAnnotationPathIsBlank() {

        FieldNameRequest request = new FieldNameRequest();
        request.setDepartment(" Engineering ");

        when(beanFactory.createBean(IdentitySearchValueTransformer.class))
                .thenReturn(new IdentitySearchValueTransformer());
        when(root.get("department")).thenReturn(firstNamePath);
        when(criteriaBuilder.equal(firstNamePath, "Engineering"))
                .thenReturn(activePredicate);

        Specification<User> specification = new GenericSearchBuilder(beanFactory).build(request);

        Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);

        assertThat(predicate).isSameAs(activePredicate);
        verify(root).get("department");
    }

    @Test
    void buildReadsAnnotatedFieldsFromSuperclass() {

        ChildSearchRequest request = new ChildSearchRequest();
        request.setInheritedValue(" inherited ");

        when(beanFactory.createBean(IdentitySearchValueTransformer.class))
                .thenReturn(new IdentitySearchValueTransformer());
        when(root.get("inheritedValue")).thenReturn(firstNamePath);
        when(criteriaBuilder.equal(firstNamePath, "inherited"))
                .thenReturn(activePredicate);

        Specification<User> specification = new GenericSearchBuilder(beanFactory).build(request);

        assertThat(specification.toPredicate(root, query, criteriaBuilder))
                .isSameAs(activePredicate);
    }

    @Test
    void buildDoesNotCreateSpecificationForNullAndBlankValues() {

        UserSearchRequest request = new UserSearchRequest();
        request.setFirstName(null);
        request.setLastName("   ");

        when(criteriaBuilder.conjunction()).thenReturn(combinedPredicate);

        Specification<User> specification = new GenericSearchBuilder(beanFactory).build(request);

        assertThat(specification.toPredicate(root, query, criteriaBuilder))
                .isSameAs(combinedPredicate);
    }

    @Test
    void buildPassesRawNonStringValueToTransformer() {

        UserSearchRequest request = new UserSearchRequest();
        request.setActive(false);

        when(beanFactory.createBean(ActiveStatusSearchValueTransformer.class))
                .thenReturn(new ActiveStatusSearchValueTransformer());
        when(root.get("userStatus")).thenReturn(userStatusPath);
        when(criteriaBuilder.equal(userStatusPath, UserStatus.INACTIVE))
                .thenReturn(activePredicate);

        Specification<User> specification = new GenericSearchBuilder(beanFactory).build(request);

        assertThat(specification.toPredicate(root, query, criteriaBuilder))
                .isSameAs(activePredicate);
        verify(criteriaBuilder).equal(userStatusPath, UserStatus.INACTIVE);
    }

    @Test
    void buildCombinesSpecificationsWithAnd() {

        UserSearchRequest request = new UserSearchRequest();
        request.setActive(true);
        request.setRoleCode("EMPLOYEE");

        when(beanFactory.createBean(IdentitySearchValueTransformer.class))
                .thenReturn(new IdentitySearchValueTransformer());
        when(beanFactory.createBean(ActiveStatusSearchValueTransformer.class))
                .thenReturn(new ActiveStatusSearchValueTransformer());
        when(root.get("userStatus")).thenReturn(userStatusPath);
        when(criteriaBuilder.equal(userStatusPath, UserStatus.ACTIVE))
                .thenReturn(activePredicate);
        when(root.join("userRoles", jakarta.persistence.criteria.JoinType.LEFT))
                .thenReturn(userRolesJoin);
        when(userRolesJoin.join("role", jakarta.persistence.criteria.JoinType.LEFT))
                .thenReturn(roleJoin);
        when(roleJoin.get("roleCode")).thenReturn(roleCodePath);
        when(criteriaBuilder.equal(roleCodePath, "EMPLOYEE"))
                .thenReturn(roleCodePredicate);
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class)))
                .thenReturn(combinedPredicate);

        Specification<User> specification = new GenericSearchBuilder(beanFactory).build(request);

        specification.toPredicate(root, query, criteriaBuilder);

        ArgumentCaptor<Predicate> leftPredicateCaptor = ArgumentCaptor.forClass(Predicate.class);
        ArgumentCaptor<Predicate> rightPredicateCaptor = ArgumentCaptor.forClass(Predicate.class);
        verify(criteriaBuilder).and(leftPredicateCaptor.capture(), rightPredicateCaptor.capture());
        assertThat(leftPredicateCaptor.getValue()).isSameAs(activePredicate);
        assertThat(rightPredicateCaptor.getValue()).isSameAs(roleCodePredicate);
    }

    private static class FieldNameRequest {

        @SearchField(path = " ")
        private String department;

        void setDepartment(String department) {

            this.department = department;
        }
    }

    private static class ParentSearchRequest {

        @SearchField
        private String inheritedValue;

        void setInheritedValue(String inheritedValue) {

            this.inheritedValue = inheritedValue;
        }
    }

    private static final class ChildSearchRequest extends ParentSearchRequest {
    }
}
