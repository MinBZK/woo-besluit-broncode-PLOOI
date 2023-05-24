import { KeyboardEvent, FocusEvent } from "react";
import { formatDate, stringToDate } from "../../../utils/DateFormatter";
import styles from "./styles.module.scss";

interface Props {
  id?: string;
  inputRef?: (ref: HTMLInputElement) => void;
  placeholder?: string;
  onFocus?: (e: FocusEvent<HTMLInputElement, Element>) => void;
  onBlur?: () => void;
  onChange?: (value: string) => void;
  onEnter?: () => void;
  onEscape?: () => void;
  value?: string;
  type: "text" | "email" | "password" | "numeric" | "date" | "time";
  minDate?: Date;
  disabled?: boolean;
  error?: boolean;
}

export function TextInputAtom(props: Props) {
  let className = `${styles["input--text"]}`;

  if (props.error) className += ` ${styles["input--alert"]}`

  const onKeydown = (ev: KeyboardEvent<HTMLInputElement>) => {
    if (ev.key === "Enter") props.onEnter && props.onEnter();

    if (ev.key === "Escape") props.onEscape && props.onEscape();
  };

  return (
    <input
      ref={(r) => {
        props.inputRef && r && props.inputRef(r);
      }}
      disabled={props.disabled}
      onChange={(ev) => {
        if (props.type === 'date' && props.minDate && ev.currentTarget.value) {
          try {
            const date = stringToDate(ev.currentTarget.value).getTime();
            const minValue = props.minDate.getTime();

            if (date < minValue) {
              props.onChange && props.onChange("");
              return;
            }
          } catch (e) { }
        }
        props.onChange && props.onChange(ev.currentTarget.value);
      }}
      onKeyDown={onKeydown}
      value={props.value}
      type={props.type}
      id={props.id}
      name={props.id}
      className={className}
      placeholder={props.placeholder}
      aria-invalid="false"
      onFocus={props.onFocus}
      onBlur={props.onBlur}
    />
  );
}
