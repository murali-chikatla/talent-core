# RspTalentcoreUi

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 21.2.13.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Runtime configuration

This app is deployable to different frontend hosts without rebuilding for each backend URL.

The frontend reads `/app-config.json` at startup:

```json
{
  "apiBase": "https://your-backend-gateway.example.com/api",
  "environment": "production"
}
```

For static hosts, update the deployed `app-config.json` file for that environment. For Node/SSR hosting, set `API_BASE_URL` instead:

```bash
API_BASE_URL=https://your-backend-gateway.example.com/api npm run serve:ssr:rsp-talentcore-ui
```

Keep `apiBase` ending with `/api`. The default is `/api`, which works when the frontend host proxies `/api` to the backend gateway.

Recommended production setup:

- Use HTTPS for both frontend and backend.
- Prefer a gateway/proxy path like `/api` when the host supports it.
- If frontend and backend are on different domains, configure backend CORS to allow the deployed frontend origins and keep credentials disabled unless cookies are used.
- Do not rebuild the Angular app just to change backend URLs; change `app-config.json` or `API_BASE_URL`.

## Running unit tests

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
