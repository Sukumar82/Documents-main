/* eslint-disable no-unused-vars */
/* eslint-disable react/jsx-no-constructed-context-values */
import React, { useState } from 'react';

import Cookie from 'universal-cookie';
import _ from 'lodash';
// import authenticationService from '../services/authentication/authentication-service';

const AUTH_COOKIE_NAME = 'token';
const GLOBAL_FILTERS = 'globleFilters';
const GLOBAL_TILES = 'globleTiles';

const GLOBAL_SEARCH_PRODUCTS = 'globalSearchProducts';
const GLOBAL_SEARCH_VENDORS = 'globalSearchVendors';
const GLOBAL_COVERAGE_SUMMARY = 'globalConverageSummary';
const cookies = new Cookie();

export const GlobalContext = React.createContext({
  user: null,
  loginHandler: () => {},
  getUserFromLocalStorage: () => {},
});

const storeUserInLocalStorage = (userData) => {
  if (!userData) {
    localStorage.removeItem('user');
    return;
  }

  // Only storing what we actually need just now.
  const redactedUserData = {
    id: userData.id,
    email: userData.email,
    activities: userData.activities,
    type: userData.type,
  };

  if (_.has(userData, 'organisation')) {
    redactedUserData.organisation = userData.organisation;
  }

  localStorage.setItem('user', JSON.stringify(redactedUserData));
};

const getUserFromLocalStorage = () => {
  const user = sessionStorage.getItem('user'); // from session
  if (user) {
    return user;
  }

  return undefined;
};



const storeGlobalItemsInLocalStorage = (itemsNameString, items) => {
  if (!items) {
    localStorage.removeItem(itemsNameString);
    return;
  }

  localStorage.setItem(itemsNameString, JSON.stringify(items));
};

const getGlobleItemsFromLocalStorage = (itemName) => {
  const globleItems = localStorage.getItem(itemName);

  if (globleItems) {
    return JSON.parse(globleItems);
  }

  return undefined;
};

function GlobalContextProvider({ children }) {
  const [user, setUserData] = useState(getUserFromLocalStorage());
  const [globleFilters, setGlobleFilters] = useState(
    getGlobleItemsFromLocalStorage(GLOBAL_FILTERS),
  );
  const [globleTiles, setGolbleTiles] = useState(getGlobleItemsFromLocalStorage(GLOBAL_TILES));
  const [globalSearchProducts, setGlobalSearchProducts] = useState(
    getGlobleItemsFromLocalStorage(GLOBAL_SEARCH_PRODUCTS),
  );

  const [globalSearchVendors, setGlobalSearchVendors] = useState(
    getGlobleItemsFromLocalStorage(GLOBAL_SEARCH_VENDORS),
  );

  const [globalConverageSummary, setGlobalConverageSummary] = useState(
    getGlobleItemsFromLocalStorage(GLOBAL_COVERAGE_SUMMARY),
  );

  const clearItemInCookie = async (cookieItemName) => {
    storeGlobalItemsInLocalStorage(cookieItemName, undefined);
    await cookies.remove(cookieItemName);
  };
  const logoutHandler = async () => {
    clearItemInCookie(AUTH_COOKIE_NAME);
    setUserData(null);

    clearItemInCookie('user');

    clearItemInCookie(GLOBAL_FILTERS);
    setGlobleFilters(null);
    clearItemInCookie(GLOBAL_TILES);
    setGolbleTiles(null);
    clearItemInCookie(GLOBAL_SEARCH_PRODUCTS);
    setGlobalSearchProducts(null);
    clearItemInCookie(GLOBAL_SEARCH_VENDORS);
    setGlobalSearchVendors(null);

    clearItemInCookie(GLOBAL_COVERAGE_SUMMARY);
    setGlobalConverageSummary(null);

    window.location.replace('/');
  };

  // login handler, utilised by the login screen.
  const loginHandler = async () => {
    // Clear up any existing sessions before attempting to login
    storeUserInLocalStorage(undefined);
    await cookies.remove(AUTH_COOKIE_NAME);
    setUserData(null);

    // setUserData(userData);
    // storeUserInLocalStorage(userData);

    // return userData;
  };


  return (
    <GlobalContext.Provider
      value={{
        login: loginHandler,
        logout: logoutHandler,
        user,
      }}
    >
      {children}
    </GlobalContext.Provider>
  );
}

export default GlobalContextProvider;
