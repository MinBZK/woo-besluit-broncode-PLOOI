import styles from "./styles.module.scss";

interface Props {
  verticaal?: boolean;
  type?: "primary";
}

export function DividerAtom(props: Props) {
  let className = styles.divider;

  if (props.type)
    className += ` ${styles[`divider--${props.type}`]}`;


  if (props.verticaal)
    className += ` ${styles[`divider--verticaal`]}`;

  return <div className={className} />;
}
