FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build && mkdir -p /app/out && \
    if [ -d dist/rsp-talentcore-ui/browser ]; then cp -R dist/rsp-talentcore-ui/browser/. /app/out/; elif [ -d dist ]; then cp -R dist/. /app/out/; elif [ -d build ]; then cp -R build/. /app/out/; else echo "No build output directory found"; exit 1; fi

FROM node:20-alpine
WORKDIR /app
COPY --from=build /app/out /app/out
COPY server.js /app/server.js
EXPOSE 80
CMD ["node", "/app/server.js"]
