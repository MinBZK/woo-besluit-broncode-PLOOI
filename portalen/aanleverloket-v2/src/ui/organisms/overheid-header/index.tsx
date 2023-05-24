import { useAppSelector } from "../../../store/hooks";
import { selectAuth } from "../../../store/selectors";
import { HeaderMolecule } from "../../molecules/header";

export function OverheidHeaderOrganism() {
  const authorizedLinks = [
    {
      href: "#/startpagina",
      text: "Startpagina",
      selected:
        window.location.href.indexOf("Startpagina") > -1 ||
        !(
          window.location.href.indexOf("documenten") > -1 ||
          window.location.href.indexOf("aanleveren") > -1 ||
          window.location.href.indexOf("update") > -1 ||
          window.location.href.indexOf("over") > -1
        ),
    },
    {
      href: "#/documenten",
      text: "Documentenlijst",
      selected: window.location.href.indexOf("documenten") > -1,
    },
    {
      href: "#/aanleveren",
      text: "Document uploaden",
      selected: window.location.href.indexOf("aanleveren") > -1 || window.location.href.indexOf("update") > -1,
    },
    {
      href: "#/over",
      text: "Over het aanleverloket",
      selected: window.location.href.indexOf("over") > -1,
    },
  ];

  const unAuthorizedLinks = [
    {
      href: "",
      text: "Startpagina",
      selected: !(window.location.href.indexOf("over") > -1),
    },
    {
      href: "#/over",
      text: "Over het aanleverloket",
      selected: window.location.href.indexOf("over") > -1,
    },
  ];

  const state = useAppSelector(selectAuth);
  const links = state.isAuthenticated ? authorizedLinks : unAuthorizedLinks;

  return (
    <HeaderMolecule
      logo="/assets/images/overheid-logo.svg"
      subtitle="Platform Open Overheidsinformatie"
      links={links}
    />
  );
}
