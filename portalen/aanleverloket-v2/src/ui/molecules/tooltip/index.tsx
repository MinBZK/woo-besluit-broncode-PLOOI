import { useState } from "react";
import { ButtonAtom, IconAtom, LabelAtom, TooltipAtom } from "../../atoms";
import styles from "./styles.module.scss";

interface Props {
  id: string;
  content: string | string[];
}

export function TooltipMolecule(props: Props) {
  let timer: any;

  const [show, setShow] = useState(false);
  const onDismiss = () => {
    timer = setTimeout(() => setShow(false), 500);
  };

  const onEnter = () => {
    clearTimeout(timer);
    setShow(true);
  };

  return (
    <TooltipAtom onMouseEnter={onEnter} onMouseLeave={onDismiss}>
      <ButtonAtom id={props.id} onClick={() => undefined}>
        <IconAtom alt="info" icon="icon-explanation" size="small" />
      </ButtonAtom>

      {show && (
        <div className={styles.content}>
          <LabelAtom type="form">{props.content}</LabelAtom>
        </div>
      )}
    </TooltipAtom>
  );
}
