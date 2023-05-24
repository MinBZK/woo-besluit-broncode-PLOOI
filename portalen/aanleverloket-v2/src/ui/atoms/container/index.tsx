import styles from "./styles.module.scss";
import "../../columns.scss";

interface Props {
  children?: any;
  width?: number | string;
  height?: number | string;
  type?: "columns" | "row" | "flex";
  centered?: boolean;
}

export function ContainerAtom(props: Props) {
  let className = styles.container;

  if (props.type === "columns") className += " columns";

  if (props.type === "flex") className += ` ${styles["container--flex"]}`;

  if (props.centered) className += ` ${styles["container--centered"]}`;

  if (props.type === "row") className += ` ${styles["container--row"]}`;

  return (
    <div
      className={className}
      style={{
        width: props.width,
        height: props.height,
      }}
    >
      {props.children}
    </div>
  );
}
