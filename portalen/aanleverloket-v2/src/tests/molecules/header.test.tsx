import { fireEvent, getByText, render } from "@testing-library/react";
import { HeaderMolecule } from "../../ui/molecules";
import "@testing-library/jest-dom/extend-expect";
import { Provider } from "react-redux";
import {setupStore} from "../../store";
import { BrowserRouter as Router } from "react-router-dom";

const props = {
  logo: "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png",
  subtitle: "Ondertitel",
  links: [
    {
      href: "https://google.com",
      text: "Google zoeken",
      lang: "nl",
      external: false,
      newWindow: true,
    },
  ],
};

it("Renders the header", () => {
  const { getByRole } = render(
    <Router>
      <Provider store={setupStore()}>
        <HeaderMolecule {...props} />
      </Provider>
    </Router>
  );

  const logo = getByRole("img", {
    name: /overheid logo/i,
  });
  expect(
    logo.parentElement?.parentElement?.parentElement?.parentElement
  ).toBeInTheDocument();
});

// it("Renders overheid.nl button", () => {
//   const { getByRole } = render(
//     <Router>
//       <Provider store={setupStore()}>
//         <HeaderMolecule {...props} />
//       </Provider>
//     </Router>
//   );

//   const logo = getByRole("button", {
//     name: /overheid\.nl/i,
//   });
//   expect(logo.parentElement).toBeInTheDocument();
// });

it("header has class header__nav", () => {
  const { getByText } = render(
    <Router>
      <Provider store={setupStore()}>
        <HeaderMolecule {...props} />
      </Provider>
    </Router>
  );

  const overheid = getByText(/Google zoeken/i);
  expect(overheid.parentElement?.parentElement?.parentElement?.parentElement?.parentElement).toHaveClass(
    "header__nav"
  );
});


// it("open submenu onClick", () => {
//   const { getByRole, getByText } = render(
//     <Router>
//       <Provider store={setupStore()}>
//         <HeaderMolecule {...props} />
//       </Provider>
//     </Router>
//   );

//   const overheid = getByRole("button", {
//     name: /overheid\.nl/i,
//   });
//   fireEvent.click(overheid);

//   const menuItem = getByText("Berichten over uw buurt");

//   expect(menuItem).toBeInTheDocument();
// });

it("menu onClick small screen", () => {
  global.window.innerWidth = 200;

  const { getByRole, getByText } = render(
    <Router>
      <Provider store={setupStore()}>
        <HeaderMolecule {...props} />
      </Provider>
    </Router>
  );

  const menu = getByRole("button", { name: /icon\-hamburger menu/i });
  fireEvent.click(menu);

  const overheid = getByText(/Google zoeken/i);
  expect(overheid.parentElement?.parentElement?.parentElement?.parentElement).toHaveClass(
    "header__nav--menuOpened"
  );
});

// it("menu onClick small screen open submenu", () => {
//   global.window.innerWidth = 200;

//   const { getByRole, getByText } = render(
//     <Router>
//       <Provider store={setupStore()}>
//         <HeaderMolecule {...props} />
//       </Provider>
//     </Router>
//   );

//   const menu = getByRole("button", { name: /icon\-hamburger menu/i });
//   fireEvent.click(menu);

//   const overheid = getByRole("button", {
//     name: /overheid\.nl/i,
//   });
//   fireEvent.click(overheid);

//   const menuItem = getByText("Berichten over uw buurt");
//   expect(menuItem).toBeInTheDocument();
// });
