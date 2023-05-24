import { ButtonAtom, DividerAtom, IconAtom, LabelAtom } from "../../atoms";
import { SpinnerAtom } from "../../atoms/spinner";
import styles from "./styles.module.scss";

interface Props {
  id: string;
  title: string;
  onClick: () => void;
  disabled?: boolean;
  loading?: boolean;
  type: "default" | "primary" | "orange" | "blue";
}

interface IconButtonProps extends Props {
  icon: string;
  text?: string;
  rtl?: boolean;
  size?: "small" | "medium" | "large" | "xLarge" | "xxLarge" | "inputfieldSize";
  squar?: boolean;
}

interface TextButtonProps extends Props {
  text: string;
}

const mapTypes = (type: string): {
  buttonType?: "primary" | "orange" | "blue";
  dividerType?: "primary";
  labelType: "blue" | "form";
} => {
  switch (type) {

    case "primary":
      return {
        buttonType: "primary",
        labelType: "blue",
        dividerType: "primary",
      };

    case "orange":
      return {
        buttonType: "orange",
        labelType: "blue",
        dividerType: undefined,
      };

    case "blue":
      return {
        buttonType: "blue",
        labelType: "form",
        dividerType: undefined,
      };

    default:
      return {
        buttonType: undefined,
        labelType: "form",
        dividerType: undefined,
      };

  }
};

function IconButton(props: IconButtonProps) {
  const { buttonType, dividerType, labelType } = mapTypes(props.type);


  let ClassNameIconButton = styles.button__content;
  let ClassNameContent = styles.button__content;
  if (props.squar) ClassNameIconButton += ` ${styles[`button__content--squar`]}`;
  if (props.size) ClassNameContent += ` ${styles[`button__content--${props.size}`]}`;
  if (props.size === "inputfieldSize") ClassNameIconButton += ` ${styles[`button__content--${props.size}`]}`;

  return <ButtonAtom id={props.id} disabled={props.disabled} type={buttonType} onClick={props.onClick} title={props.title}>
    {
      props.loading && <LoadingOverlay {...props} />
    }
    <span className={styles.button}>
      {props.text && <>
        <div className={ClassNameContent}>
          <LabelAtom type={labelType}>{props.text}</LabelAtom>
        </div>

        <div className={styles.button__divider}>
          <DividerAtom type={dividerType} />
        </div>
      </>
      }

      <div className={ClassNameIconButton}>
        <IconAtom
          icon={props.icon}
          size={props.size ? props.size : "medium"}
          alt={props.icon}
        ></IconAtom>
      </div>
    </span>
  </ButtonAtom >;
}

function IconButtonRTL(props: IconButtonProps) {
  const { buttonType, labelType } = mapTypes(props.type);

  let ClassNameContent = styles.button__content;
  if (props.size) ClassNameContent += ` ${styles[`button__content--${props.size}`]}`;

  return (
    <ButtonAtom id={props.id} disabled={props.disabled} type={buttonType} onClick={props.onClick} title={props.title}>
      {
        props.loading && <LoadingOverlay {...props} />
      }
      <span className={styles.button}>
        <div className={`${ClassNameContent} ${styles["button__content--rtl"]}`}>
          <IconAtom
            icon={props.icon}
            size={props.size ? props.size : "medium"}
            alt={props.icon}
          />
          {
            props.text && <LabelAtom type={labelType}>{props.text}</LabelAtom>
          }
        </div>
      </span>
    </ButtonAtom>
  );
}

function LoadingOverlay(props: Props) {
  const spinnerCs = styles.button__spinner;
  let containerCs = styles.button__spinner__container;

  containerCs += props.type === 'primary' ? ` ${styles.button__spinner__container__primary}` : ` ${styles.button__spinner__container__default}`;

  return <div className={containerCs}>
    <div className={spinnerCs}>
      <SpinnerAtom type={props.type === 'primary' ? 'white' : 'primary'} />
    </div>
  </div>
}

export function IconButtonMolecule(props: IconButtonProps) {
  if (props.rtl)
    return IconButtonRTL(props);

  return IconButton(props);
}

export function ButtonMolecule(props: TextButtonProps) {
  const { buttonType, labelType } = mapTypes(props.type);

  return <ButtonAtom id={props.id} disabled={props.disabled} type={buttonType} onClick={props.onClick} title={props.title}>
    {
      props.loading && <LoadingOverlay {...props} />
    }
    <span className={styles.button}>
      <div className={styles.button__content}>
        <LabelAtom type={labelType}>{props.text}</LabelAtom>
      </div>
    </span>
  </ButtonAtom>
}