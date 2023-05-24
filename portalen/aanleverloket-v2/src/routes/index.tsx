import { HashRouter as Router, Routes, Route } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "../store/hooks";
import { selectAuth } from "../store/selectors";
import React from 'react';
import { Storage, StorageLocation } from "../utils/Storage";
import { storeToken, login } from "../store/reducers/auth";

const LoginScreen = React.lazy(() => import('../ui/screens/login'));
const StartScreen = React.lazy(() => import('../ui/screens/start'));
const DocumentenScreen = React.lazy(() => import('../ui/screens/documenten'));
const AanleverenScreen = React.lazy(() => import('../ui/screens/create'));
const UpdateScreen = React.lazy(() => import('../ui/screens/update'));
const OverScreen = React.lazy(() => import('../ui/screens/over'));

function UnauthenticatedRoutes() {
  return (
    <Routes>
       <Route path="*" element={
            <React.Suspense fallback={<>...</>}>
              <LoginScreen />
            </React.Suspense>
          }>
      </Route>
      <Route path="/over" element={
            <React.Suspense fallback={<>...</>}>
              <OverScreen />
            </React.Suspense>
          }>
      </Route>
    </Routes>
  );
}

function AuthenticatedRoutes() {
  return (
    <Routes>
      <Route path="/start" element={
            <React.Suspense fallback={<>...</>}>
              <StartScreen />
            </React.Suspense>
          }>
      </Route>
      <Route path="/documenten" element={
            <React.Suspense fallback={<>...</>}>
              <DocumentenScreen />
            </React.Suspense>
          }>
      </Route>
      <Route path="/aanleveren" element={
            <React.Suspense fallback={<>...</>}>
              <AanleverenScreen />
            </React.Suspense>
          }>
      </Route>
      <Route path="/update" element={
            <React.Suspense fallback={<>...</>}>
              <UpdateScreen />
            </React.Suspense>
          }>
      </Route>
      <Route path="/over" element={
            <React.Suspense fallback={<>...</>}>
              <OverScreen />
            </React.Suspense>
          }>
      </Route>
      <Route path="*" element={
            <React.Suspense fallback={<>...</>}>
              <StartScreen />
            </React.Suspense>
          }>
      </Route>
    </Routes>
  );
}

export default function RootRouter() {

  const { isAuthenticated } = useAppSelector(selectAuth);
  
  const dispatch = useAppDispatch();

  const authRoutes = AuthenticatedRoutes();
  const unauthRoutes = UnauthenticatedRoutes();

  React.useEffect(() => {
    const token = Storage.Get(StorageLocation.TOKEN);
    const currentUrl = window.location.href;
    const accessToken = currentUrl.indexOf('?') > -1
        ? new URLSearchParams(currentUrl.split('?')[1]).get('access_token')
        : undefined;

    if (token)
    {
      dispatch(login());
    }
    else if(accessToken)
    {
      dispatch(storeToken(accessToken));
    }
  }, []);

  


  
  return (
    <Router>
      {isAuthenticated && authRoutes}
      {!isAuthenticated && unauthRoutes}
    </Router>
  );
}
