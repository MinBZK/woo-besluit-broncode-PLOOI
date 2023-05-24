import { LabelMolecule } from "..";
import { TextAreaAtom } from "../../atoms";
import styles from "./styles.module.scss";

interface Props {
  value: string;
  placeholder: string;
  onChange: (text: string) => void;

  tooltip?: string;
  label?: string;
  required?: boolean;
  id?: string;
}

export function TextAreaMolecule(props: Props) {
  return (
    <div className={styles.area}>
      <LabelMolecule
        id={props.id}
        label={props.label ?? ""}
        tooltip={props.tooltip}
        required={props.required}
      />

      <TextAreaAtom
        id={props.id}
        placeholder={props.placeholder}
        onChange={props.onChange}
        value={props.value}
      />
    </div>
  );
}
