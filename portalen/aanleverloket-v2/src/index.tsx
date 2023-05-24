import React from 'react';
import ReactDOM from 'react-dom/client';
import { setupStore } from './store';
import { Provider } from 'react-redux';
import App from './routes';
import settings from "../package.json";

console.log('================ STARTUP INFO =============');
console.log(`Version: ${settings.version}`);
console.log('================ STARTUP INFO =============');

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

const store = setupStore();

root.render(
  <React.StrictMode>
    <Provider store={store}>
      <App />
    </Provider>
  </React.StrictMode>
);
