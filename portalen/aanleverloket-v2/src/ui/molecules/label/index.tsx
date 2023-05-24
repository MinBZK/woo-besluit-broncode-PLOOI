import { LabelAtom, SmallAtom } from "../../atoms";
import { TooltipMolecule } from "../tooltip";
import styles from "./styles.module.scss";

interface Props {
  id?: string;
  label: string;
  tooltip?: string | string[];
  required?: boolean;
}

export function LabelMolecule(props: Props) {
  return (
    <div className={styles.container}>
      {props.label && (
        <LabelAtom type="form" for={props.id} bold>
          {props.label}
        </LabelAtom>
      )}
      {props.tooltip && <TooltipMolecule id={props.id + "-tooltip"} content={props.tooltip} />}
      {props.required && (
        <>
          {" "}
          <div className={styles.required}></div>
          <SmallAtom text="verplicht veld" />
        </>
      )}
    </div>
  );
}
