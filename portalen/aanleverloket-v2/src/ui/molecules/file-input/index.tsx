import { useState } from "react";
import { IconLabelMolecule, LabelMolecule } from "..";
import { IFileValidator } from "../../../models/validator";
import { FileInputAtom, HeadingPreviewAtom, LabelAtom } from "../../atoms";
import styles from "./styles.module.scss";
interface Props {
  onSelectFile: (f?: File) => void;
  selectedFileName?: string;
  label?: string;
  tooltip?: string;
  required?: boolean;
  validations?: IFileValidator[];
  inputRef?: any;
}

export function FileInputMolecule(props: Props) {
  const [error, setError] = useState<string>();

  let className = styles.input_container__inner;
  if (error) className += ` ${styles["input_container__inner--alert"]}`;

  const onSelectFile = (f?: File) => {

    const errors = props.validations?.map(v => v.validate(f));

    if (errors && errors.length > 0)
      setError(errors.filter(e => e)[0]);
    else
      setError(undefined);

    props.onSelectFile(f);
  };

  return (
    <div>
      <LabelMolecule
        id={"file-input-label"}
        label={props.label ?? ""}
        tooltip={props.tooltip}
        required={props.required}
      />
      <div className={styles.input_container}>
        <FileInputAtom inputRef={props.inputRef} accept=".pdf, .zip" onFileChanged={onSelectFile}>
          {props.selectedFileName && (
            <div className={styles.input_container__selectedFileContainer}>
              <HeadingPreviewAtom>{props.selectedFileName}</HeadingPreviewAtom>
              <LabelAtom italic underlined type="form">
                Selecteer een ander bestand
              </LabelAtom>
            </div>
          )}
          {!props.selectedFileName && (
            <div className={className}>
              <LabelAtom italic type="form">
                Upload een nieuw bestand:
              </LabelAtom>
              <LabelAtom italic underlined type="form">
                Selecteer een bestand
              </LabelAtom>
            </div>
          )}
        </FileInputAtom>
      </div>
      {error && (
        <IconLabelMolecule
          icon={"icon-alert-red"}
          label={error}
          type={"alert"}
        />
      )}
    </div>
  );
}
