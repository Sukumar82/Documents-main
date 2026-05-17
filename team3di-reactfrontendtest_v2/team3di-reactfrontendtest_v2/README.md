# 3di React Test
A modern React application built for Node.js 18. 

# Prerequisites
Node.js: Version 18.x.x (LTS recommended).
npm: Version 8.x or higher. 


# Running in Visual Studio
Open Folder: Launch VS Code and select File > Open Folder..., then choose the project root.
Integrated Terminal: Open the terminal with Ctrl + Shift + ` .
Install & Run:
Type npm install and press Enter.
Type npm start (or npm run dev for Vite) to launch the app.
Debugging: Go to the Run and Debug view (Ctrl + Shift + D) and click Create a launch.json file to enable browser debugging directly within the editor. 

# Visual Studio 2022
Open Project: Select File > Open > Web Site... or Folder... and select the project root.
Using Templates: If creating a new project, search for the "React App" template.
Install Dependencies: Right-click the project in Solution Explorer and select Open in Terminal. Run npm install.
Run: Press F5 or click the Start button in the toolbar. Visual Studio will attempt to start the Node.js server and open your default browser. 

# Available Scripts
npm start: Runs the app in development mode.
npm test: Launches the test runner.
npm run build: Builds the app for production to the build/ (or dist/) folder. 


# Assessment Instructions

- Display the current balance in the account.currentBalance model

- Display transactions between start and end date – 
  The frontend should have two new inputs to take the start and end date. This should be sent to the backend including the existing form submission data
  Update the backend to receive and use the start and end date to only load transactions where the initiation date is between the start and end date. The start and end dates can be inclusive of the data returned. E.g. if start date is 2019-11-04 then it should also include records which have the initiation date 2019-11-04.


# Getting Started with Create React App

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Available Scripts

In the project directory, you can run:

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

The page will reload when you make changes.\
You may also see any lint errors in the console.

### `npm test`

Launches the test runner in the interactive watch mode.\
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.\
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can't go back!**

If you aren't satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point you're on your own.

You don't have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn't feel obligated to use this feature. However we understand that this tool wouldn't be useful if you couldn't customize it when you are ready for it.

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).

### Code Splitting

This section has moved here: [https://facebook.github.io/create-react-app/docs/code-splitting](https://facebook.github.io/create-react-app/docs/code-splitting)

### Analyzing the Bundle Size

This section has moved here: [https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size](https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size)

### Making a Progressive Web App

This section has moved here: [https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app](https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app)

### Advanced Configuration

This section has moved here: [https://facebook.github.io/create-react-app/docs/advanced-configuration](https://facebook.github.io/create-react-app/docs/advanced-configuration)

### Deployment

This section has moved here: [https://facebook.github.io/create-react-app/docs/deployment](https://facebook.github.io/create-react-app/docs/deployment)

### `npm run build` fails to minify

This section has moved here: [https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify](https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify)
