import { TextInputAtom } from "../../atoms";
import { LabelMolecule } from "..";
import styles from "./styles.module.scss";
import { IconLabelMolecule } from "../icon-label";
import { useState } from "react";
import { ITextValidator } from "../../../models/validator";

interface Props {
  id?: string;
  placeholder?: string;
  onChange?: (value: string) => void;
  onEnter?: () => void;
  value?: string;
  label?: string;
  tooltip?: string | string[];
  required?: boolean;
  disabled?: boolean;
  inputRef?: (ref: HTMLInputElement) => void;
  validations?: ITextValidator[];
}

interface InputProps extends Props {
  type: "text" | "email" | "password" | "date" | "time";
}

function InputMolecule(props: InputProps) {

  const [hasBlurred, setHasBlurred] = useState(false);
  const onBlur = () => setHasBlurred(true);
  const errors = hasBlurred ? props.validations?.map(v => {
    if (props.type !== 'date')
      return v.validate(props.value ?? "");

    else if (props.value?.indexOf("NaN") === -1) {
      return v.validate(props.value);
    } else
      return v.validate("");

  }).filter(v => v != undefined) : [];
  const hasError = errors && errors?.length > 0;

  const textInputField = (
    <TextInputAtom
      {...props}
      error={hasError}
      onBlur={onBlur}
      onChange={props.onChange}
    />
  );

  return (
    <div className={styles.container}>
      {props.label && <LabelMolecule
        id={props.id}
        label={props.label}
        tooltip={props.tooltip}
        required={props.required}
      />}
      {textInputField}
      {hasError && (
        <IconLabelMolecule
          icon={"icon-alert-red"}
          label={errors[0] ?? ""}
          type={"alert"}
        />
      )}
    </div>
  );
}

export function PasswordInputMolecule(props: Props) {
  return <InputMolecule {...props} type="password" />;
}

export function EmailInputMolecule(props: Props) {
  return <InputMolecule {...props} type="email" />;
}

export function TextInputMolecule(props: Props) {
  return <InputMolecule {...props} type="text" />;
}

export function DateInputMolecule(props: Props & { minDate?: Date }) {
  return <InputMolecule {...props} type="date" />;
}

export function TimeInputMolecule(props: Props & { minDate?: Date }) {
  return <InputMolecule {...props} type="time" />;
}
