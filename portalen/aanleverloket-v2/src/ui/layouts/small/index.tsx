import styles from "../layouts.module.scss";

interface Props {
  header: JSX.Element;
  body: JSX.Element;
  footer?: JSX.Element;
}

export function SmallLayout(props: Props) {

  let className = styles.layouts__centered__body;
  if(!props.footer) className += ` ${styles.layouts__page__padding}`

  return (
    <div className={styles.layouts__page}>
      {props.header}

      <span className={className}>
        <span className={styles.layouts__page__small}>
            {props.body}
        </span>
      </span>

      {props.footer}
    </div>
  );
}
