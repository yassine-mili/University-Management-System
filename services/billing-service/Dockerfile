FROM mcr.microsoft.com/dotnet/sdk:7.0 AS build
WORKDIR /src

COPY BillingService.csproj ./
RUN dotnet restore "BillingService.csproj"
COPY . ./
RUN dotnet publish "BillingService.csproj" -c Release -o /app/publish

FROM mcr.microsoft.com/dotnet/aspnet:7.0 AS runtime
WORKDIR /app
COPY --from=build /app/publish .
COPY ./entrypoint.sh ./entrypoint.sh
RUN chmod +x ./entrypoint.sh
ENV ASPNETCORE_URLS=http://+:5000
EXPOSE 5000
ENTRYPOINT ["/bin/bash", "./entrypoint.sh"]
