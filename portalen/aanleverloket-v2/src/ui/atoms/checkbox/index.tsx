import { ICheckbox } from "../../interfaces/Checkbox";
import styles from "./styles.module.scss";

export function CheckboxAtom(props: ICheckbox) {
  let containerClassName = styles.checkbox__container;

  if (props.checked) containerClassName += ` ${styles["checkbox--checked"]}`;
  if (props.disabled) containerClassName += ` ${styles["checkbox--disabled"]}`;

  let checkboxClassname = styles.checkbox;

  if (props.disabled) {
    if (!props.checked) checkboxClassname += ` ${styles["checkbox--white"]}`;
    else checkboxClassname += ` ${styles["checkbox--gray"]}`;

    checkboxClassname += ` ${styles["checkbox--disabled"]}`;
  } else {
    if (!props.checked) {
      checkboxClassname += ` ${styles["checkbox--white"]}`;
      containerClassName += ` ${styles["checkbox__container__hover"]}`;
    } else checkboxClassname += ` ${styles["checkbox--blue"]}`;
  }

  return (
    <div className={containerClassName}>
      <div
        id={props.id}
        title={props.label}
        className={checkboxClassname}
        onClick={props.disabled ? undefined : props.onClick}
      >
        {props.checked && (
          <img
            alt="checked"
            className={styles.checked__image}
            src="/assets/icons/icon-checkbox-white.svg"
          />
        )}
      </div>

      {props.label && (
        <label
          className={styles.checkbox__label}
          htmlFor={props.id}
          onClick={props.disabled ? undefined : props.onClick}
        >
          {props.label}
        </label>
      )}
    </div>
  );
}
