
FROM SSSSSSSSSSSSSSSSSS/koop-plooi-docker-hub/library/node:17-alpine as BUILD
#FROM node:17-alpine as BUILD

WORKDIR /app
COPY . ./
RUN npm ci --legacy-peer-deps

# === Run tests
#RUN npm test

# === BUILD Project + SB
RUN npm run build
#RUN npm run build-storybook

# production environment
FROM SSSSSSSSSSSSSSSSSS/koop-plooi-docker-hub/library/nginx:latest
#FROM nginx:latest

COPY --from=BUILD /app/build /usr/share/nginx/html
#COPY --from=BUILD /app/storybook-static /usr/share/nginx/html/storybook

COPY nginx/non-root.conf /etc/nginx/nginx.conf
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
