import { ToastType } from "../../interfaces/Toast";
import styles from "./styles.module.scss";

interface Props {
  type: ToastType;
  children?: any;
}

export function ToastAtom(props: Props) {
  const className = `${styles.toaster} ${styles[`toaster--${props.type}`]}`;

  return <div className={className}>{props.children}</div>;
}
