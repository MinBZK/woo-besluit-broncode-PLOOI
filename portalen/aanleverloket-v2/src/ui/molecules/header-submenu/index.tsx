import { ContainerAtom } from "../../atoms";
import { IExternalLink } from "../../interfaces/Link";
import { ListMolecule } from "../../molecules";
import styles from "./styles.module.scss";

interface Props {
  open: boolean;
}

export function HeaderSubmenuMolecule(props: Props) {
  let className = `${styles.submenu}`;

  if (props.open) className += ` ${styles.submenu__open}`;

  const menuItemsColumn1: IExternalLink[] = [
    {
      href: "https://www.overheid.nl/berichten-over-uw-buurt",
      text: "Zoek naar berichten",
      newWindow: true,
      external: true,
    },
    {
      href: "https://www.overheid.nl/berichten-over-uw-buurt#meld-u-aan-voor-de-e-mailservice",
      text: "Blijf op de hoogte",
      newWindow: true,
      external: true,
    },
  ];

  const menuItemsColumn2: IExternalLink[] = [
    {
      href: "https://www.overheid.nl/dienstverlening",
      text: "Naar dienstverlening",
      newWindow: true,
      external: true,
    },
  ];

  const menuItemsColumn3: IExternalLink[] = [
    {
      href: "https://www.overheid.nl/beleid-en-regelgeving",
      text: "Naar beleid & regelgeving",
      newWindow: true,
      external: true,
    },
  ];

  const menuItemsColumn4: IExternalLink[] = [
    {
      href: "https://organisaties.overheid.nl/",
      text: "Naar overheidsorganisaties",
      newWindow: true,
      external: true,
    },
  ];

  return !props.open ? (
    <></>
  ) : (
    <div className={className}>
      <ContainerAtom centered type="row">
        <div>
          <h2>Berichten over uw buurt</h2>
          <p>Zoals bouwplannen en verkeersmaatregelen.</p>
          <ListMolecule linked links={menuItemsColumn1} />
        </div>
        <div>
          <h2>Dienstverlening</h2>
          <p>Zoals belastingen, uitkeringen en subsidies.</p>
          <ListMolecule linked links={menuItemsColumn2} />
        </div>
        <div>
          <h2>Beleid &amp; regelgeving</h2>
          <p>Officiële publicaties van de overheid.</p>
          <ListMolecule linked links={menuItemsColumn3} />
        </div>
        <div>
          <h2>Contactgegevens overheden</h2>
          <p>Adressen en contactpersonen van overheidsorganisaties.</p>
          <ListMolecule linked links={menuItemsColumn4} />
        </div>
      </ContainerAtom>
    </div>
  );
}
