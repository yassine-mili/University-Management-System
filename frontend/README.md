# University Management System - Frontend

React + TypeScript frontend for the University Management System.

## Features

- **Authentication**: Login/Register with JWT
- **Student Portal**: View courses, grades, and invoices
- **Teacher Portal**: Manage courses and grade students
- **Admin Portal**: System management and analytics
- **Responsive Design**: Material-UI components

## Prerequisites

- Node.js 18+
- npm or yarn

## Installation

```bash
npm install
```

## Development

```bash
npm start
```

Runs the app in development mode at [http://localhost:3000](http://localhost:3000)

## Build

```bash
npm run build
```

Creates an optimized production build in the `build` folder.

## Environment Variables

Create a `.env` file:

```
REACT_APP_API_URL=http://localhost:8080
REACT_APP_AUTH_URL=http://localhost:8081
REACT_APP_STUDENT_URL=http://localhost:8082
REACT_APP_COURSES_URL=http://localhost:8083
REACT_APP_GRADES_URL=http://localhost:8084
REACT_APP_BILLING_URL=http://localhost:5000
```

## Docker

Build and run with Docker:

```bash
docker build -t ums-frontend .
docker run -p 3000:80 ums-frontend
```

## Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/       # Reusable components
│   ├── context/          # React contexts
│   ├── pages/            # Page components
│   ├── services/         # API services
│   ├── types/            # TypeScript types
│   ├── utils/            # Utility functions
│   ├── App.tsx           # Main app component
│   └── index.tsx         # Entry point
├── Dockerfile
├── package.json
└── tsconfig.json
```

## Available Scripts

- `npm start` - Start development server
- `npm build` - Build for production
- `npm test` - Run tests
- `npm run eject` - Eject from Create React App

## API Integration

The frontend connects directly to backend services:

- **Auth Service** (8081): User authentication
- **Student Service** (8082): Student management
- **Courses Service** (8083): Course management (SOAP)
- **Grades Service** (8084): Grade management
- **Billing Service** (5000): Invoice and payments (SOAP)

## Technologies

- React 18
- TypeScript
- Material-UI
- React Router
- Axios
- React Context API
