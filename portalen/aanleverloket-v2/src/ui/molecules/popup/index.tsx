import { ContainerAtom, HeadingH2Atom, IconAtom } from "../../atoms";
import { ButtonMolecule, IconButtonMolecule } from "../button";
import styles from "./styles.module.scss";

interface Props {
  title: string;
  text: string;
  cancelText: string;
  okeText?: string;
  cancelButton: () => void;
  okeButton?: () => void;
  extraInfo?: any;
}

export function PopupMolecule(props: Props) {
  return (
    <div className={styles.popup}>
      <div className={styles.popup_inner}>
        <div className={styles.blue_inner}>
          <div className={styles.text}>
            <ContainerAtom type="row">
              <HeadingH2Atom>{props.title}</HeadingH2Atom>
              <div onClick={props.cancelButton} className={styles.right}>
                <IconAtom icon={"icon-close-hover"} size={"small"} alt={""} />
              </div>
            </ContainerAtom>
            <p>{props.text}</p>
            <div>{props.extraInfo}</div>
          </div>

          {props.okeButton && (
            <div className={styles.right}>
              <ButtonMolecule
                text={props.okeText ?? "Oke"}
                id={"oke-button"}
                title={"Oke"}
                onClick={props.okeButton}
                type={"primary"}
              />
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
