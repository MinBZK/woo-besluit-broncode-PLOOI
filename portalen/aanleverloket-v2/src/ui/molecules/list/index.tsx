import { ListAtom, ListItemAtom, LinkAtom, LogoAtom } from "../../atoms";

import { IExternalLink } from "../../interfaces/Link";
import styles from "./styles.module.scss";

interface Props {
  links: IExternalLink[];
  unstyled?: boolean;
  linked?: boolean;
}

export function ListMolecule(props: Props) {
  const { links } = props;

  return (
    <ListAtom linked={props.linked} unstyled={props.unstyled}>
      {links.map((item, itemIndex) => (
        <ListItemAtom link={props.linked} key={itemIndex}>
          <LinkAtom {...item} />
          {item.external === true && (
            <LogoAtom
              alt="externe link"
              className={styles.external}
              size="icon"
              src="/assets/icons/icon-link-external-v2.svg"
            />
          )}
        </ListItemAtom>
      ))}
    </ListAtom>
  );
}
