import { render } from "@testing-library/react";
import { ToastOrganism } from "../../ui/organisms";
import "@testing-library/jest-dom/extend-expect";
import { Provider } from "react-redux";
import { setupStore } from "../../store";
import { setToast } from "../../store/reducers/toast";


const delay = (ms: number | undefined) => new Promise(
  resolve => setTimeout(resolve, ms)
);

it("Renders the Toaster", async () => {
  const store = setupStore();
  await store.dispatch(
    setToast({
      autoClose: false,
      type: "error",
      message: {
        message: "Niet alle benodigde velden zijn ingevuld.",
      },
    })
  );

  const { getByText } = render(
    <Provider store={store}>
      <ToastOrganism />
    </Provider>
  );

  const text = getByText(/Niet alle benodigde velden zijn ingevuld/i);
  expect(text.parentElement).toBeInTheDocument();
});

it("Renders the Toaster and autoclose", async () => {
  const store = setupStore();
  await store.dispatch(
    setToast({
      autoClose: true,
      type: "error",
      message: {
        message: "Niet alle benodigde velden zijn ingevuld.",
      },
    })
  );

  const { getByText } = render(
    <Provider store={store}>
      <ToastOrganism />
    </Provider>
  );

  const text = getByText(/Niet alle benodigde velden zijn ingevuld/i);
  expect(text.parentElement).toBeInTheDocument();

  await delay(7000); 

  expect(text.parentElement).not.toBeInTheDocument();
}, 10000);
