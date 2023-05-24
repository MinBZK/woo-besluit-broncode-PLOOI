import { render } from "@testing-library/react";
import { HeaderSubmenuMolecule } from "../../ui/molecules";
import "@testing-library/jest-dom/extend-expect";



it("Renders the HeaderSubmenuMolecule", () => {
  const { getByRole } = render(<HeaderSubmenuMolecule open={true} />);

  const menu = getByRole('heading', { name: /berichten over uw buurt/i });
  expect(menu.parentElement?.parentElement?.parentElement).toBeInTheDocument();
});

it("header has class submenu", () => {
  const { getByRole } = render(<HeaderSubmenuMolecule open={true} />);

  const menu = getByRole('heading', { name: /berichten over uw buurt/i });
  expect(menu.parentElement?.parentElement?.parentElement).toHaveClass("submenu");
});

it("header has class submenu__open", () => {
  const { getByRole } = render(<HeaderSubmenuMolecule open={true} />);

  const menu = getByRole('heading', { name: /berichten over uw buurt/i });
  expect(menu.parentElement?.parentElement?.parentElement).toHaveClass("submenu__open");
});
