import { IconAtom, LabelAtom } from "../../atoms";
import styles from "./styles.module.scss";

interface Props {
  icon: string;
  label: string;
  type: "default" | "alert";
}

export function IconLabelMolecule(props: Props) {
  let className = styles.container;

  if (props.type !== "default") className += ` ${styles[`container--${props.type}`]}`;

  return (
    <div className={className}>
      <IconAtom icon={props.icon} size={"xLarge"} alt={props.icon} />
      <LabelAtom children={props.label} type={"form"} />
    </div>
  );
}
