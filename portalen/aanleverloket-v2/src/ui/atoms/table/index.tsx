import { ContainerAtom } from "../container";
import styles from "./styles.module.scss";

interface Props {
  theads: any[];
  rows: any[][];
  columnWidth?: string[];
  highlight?: number;
}

export function TableAtom(props: Props) {
  const headerRow = props.theads.map((i, index) => {
    return (
      <th
        className={styles.header_cell}
        style={
          props.columnWidth && index <= props.columnWidth.length
            ? { width: props.columnWidth[index] }
            : {}
        }
      >
        <div className={styles.item}>{i}</div>
      </th>
    );
  });

  const rows = props.rows.map((row, indexRow) => (
    <tr className={props.highlight != undefined && indexRow === props.highlight ? styles.highlight : ""}>
      {row.map((r, index) => {
        return (
          <td
            className={styles.cell}
            style={
              props.columnWidth && index <= props.columnWidth.length
                ? { width: props.columnWidth[index] }
                : {}
            }
          >
            <div className={styles.item}>{r}</div>
          </td>
        );
      })}
    </tr>
  ));

  return (
    <ContainerAtom>
      <div className={styles.container}>
        <table>
          <tr className={styles.header}>{headerRow}</tr>
          {rows}
        </table>
      </div>
    </ContainerAtom>
  );
}
