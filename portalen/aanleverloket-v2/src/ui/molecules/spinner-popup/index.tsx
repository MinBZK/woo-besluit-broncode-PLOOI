import { SpinnerAtom } from "../../atoms";
import styles from "./styles.module.scss";

interface Props {
  text?: string;
}

export function SpinnerPopupMolecule(props: Props) {
  return (
    <div className={styles.popup}>
      <div className={styles.popup_inner}>
        <div className={styles.spinner}>
          <SpinnerAtom type={"primary"} size={"large"} />
        </div>
        {!props.text || props.text == ""
          ? "Eén moment geduld, document wordt geüpload"
          : props.text}
      </div>
    </div>
  );
}
