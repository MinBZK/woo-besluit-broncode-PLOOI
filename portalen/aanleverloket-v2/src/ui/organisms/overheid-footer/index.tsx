import { IExternalLink, ILink } from "../../interfaces/Link";
import { FooterMolecule } from "../../molecules";

export function OverheidFooterOrganism() {
  const menu1: ILink[] = [{
    text: "Over deze website",
    href: "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"
  },
  {
    text: "Contact",
    href: "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"
  },
  {
    text: "English",
    href: "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
    lang: "en"
  },
  {
    text: "Help",
    href: "SSSSSSSSSSSSSSSSSSSSSSSSSSSSS"
  },
  {
    text: "Zoeken",
    href: "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"
  }];

  const menu2: ILink[] = [{
    text: "Informatie Hergebruiken",
    href: "/informatie-hergebruiken"
  },
  {
    text: "Privacy en cookies",
    href: "/privacy-statement"
  },
  {
    text: "Toegankelijkheid",
    href: "/toegankelijkheid"
  },
  {
    text: "Sitemap",
    href: "/sitemap"
  }];

  const menu3: ILink[] = [
    {
      text: "Open Data",
      href: "https://data.overheid.nl"
    },
    {
      text: "Linked Data Overheid",
      href: "http://linkeddata.overheid.nl"
    },
    {
      text: "PUC Open Data",
      href: "https://puc.overheid.nl"
    }
  ];

  const menu4: IExternalLink[] = [
    {
      text: "MijnOverheid.nl",
      href: "https://mijn.overheid.nl"
    },
    {
      text: "Rijksoverheid.nl",
      href: "https://www.rijksoverheid.nl",
      external: true
    },
    {
      text: "Ondernemersplein.nl",
      href: "https://ondernemersplein.nl",
      external: true
    },
    {
      text: "Werkenbijdeoverheid.nl",
      href: "https://www.werkenbijdeoverheid.nl",
      external: true
    }
  ];

  return <FooterMolecule menus={[
    menu1,
    menu2,
    menu3,
    menu4
  ]} />
}
