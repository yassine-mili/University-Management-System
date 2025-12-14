FROM mcr.microsoft.com/dotnet/aspnet:7.0 AS runtime
WORKDIR /app

FROM mcr.microsoft.com/dotnet/sdk:7.0 AS build
WORKDIR /src
COPY . .
RUN dotnet publish -c Release -o /app

FROM runtime
WORKDIR /app
COPY --from=build /app .
ENTRYPOINT ["dotnet", "BillingService.dll"]
