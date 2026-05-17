import React, { useContext } from 'react';
import { Switch, Route } from 'react-router-dom';
import TransactionTablePage from '../pages/TransactionTablePage'

function Routes() {
  return (
    <Route render={() => (
      <Switch>
        <Route exact path="/" component={TransactionTablePage} />

        {/* <Route path="*" component={NoRouteFound} /> */}
      </Switch>
    )}
    />
  );
}

export default Routes;
