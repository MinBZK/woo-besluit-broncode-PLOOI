import { HeadingH2Atom } from "../../atoms";
import { IconButtonMolecule } from "../button";
import styles from "./styles.module.scss";

interface Props {
  subheader?: string;
  header: string;
  intro: string[] | string;
  link?: any;
  center?: boolean;
}

export function JumbotronMolecule(props: Props) {
  let className = styles.container;
  if (props.center) className += ` ${styles[`center`]}`;

  return (
    <div className={className}>
      <i>{props.subheader}</i>
      <HeadingH2Atom>{props.header}</HeadingH2Atom>
      {Array.isArray(props.intro) ? (
        props.intro.map((text) => <p>{text}</p>)
      ) : (
        <p>{props.intro}</p>
      )}
      <u>{props.link}</u>
    </div>
  );
}
