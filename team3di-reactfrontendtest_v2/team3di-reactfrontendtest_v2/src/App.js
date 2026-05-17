import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import GlobalContextProvider from './context/GlobalContext';
import Routes from './routes/Routes';

function App() {
  return (
    // hoc
    <GlobalContextProvider>
      <Router>
        <Routes />
      </Router>
    </GlobalContextProvider>
  );
}

export default App;
