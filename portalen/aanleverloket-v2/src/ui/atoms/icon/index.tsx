import styles from "./styles.module.scss";

interface Props {
  icon: string;
  size: "small" | "medium" | "large" | "xLarge" | "xxLarge" | "inputfieldSize";
  alt: string;
}

export function IconAtom(props: Props) {
  const className = `${styles.icon} ${styles[`icon--${props.size}`]}`;
  const src = `/assets/icons/${props.icon}.svg`

  return <img className={className} alt={props.alt} src={src} />;
}
