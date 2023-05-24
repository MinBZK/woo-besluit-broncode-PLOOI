import styles from "./styles.module.scss";

interface Props {
  id: string;
  onClick: () => void;
  children: any;
  type?: "default" | "primary" | "unstyled" | "orange" | "blue";
  disabled?: boolean;
  title?: string;
}

export function ButtonAtom(props: Props) {
  let className: string = styles.button;

  className += ` ${styles[`button--${props.type}`]}`;
  
  return (
    <button id={props.id} type="button" disabled={props.disabled} className={className} onClick={props.onClick} title={props.title}>
      {props.children}
    </button>
  );
}
