import styles from "./styles.module.scss";
import {
  AutoSizer as _AutoSizer,
  Column as _Column,
  Table as _Table,
  AutoSizerProps,
  ColumnProps,
  TableProps,
} from "react-virtualized";
import { FC } from "react";
import scrollbarSize from "dom-helpers/scrollbarSize";

interface Props {
  theads: any[];
  rows: any[][];
  columnWidth?: string[];
  OnEndOfScroll?: () => void;
}

export function VirtualTableAtom(props: Props) {
  const Column = _Column as unknown as FC<ColumnProps>;
  const Table = _Table as unknown as FC<TableProps>;
  const AutoSizer = _AutoSizer as unknown as FC<AutoSizerProps>;

  return (
    <div className={styles.container}>
      <AutoSizer>
        {({ width, height }) => {
          return (
            <Table
              width={width - scrollbarSize()}
              height={height * 0.99}
              rowHeight={82}
              headerHeight={82}
              rowCount={props.rows.length}
              rowGetter={(rowProps) => {
                return props.rows[rowProps.index];
              }}
              rowRenderer={({ key, index, style }) => (
                <tr key={key} style={style}>
                  <div className={styles.row}>
                    {props.rows[index].map((item, i) => (
                      <div
                        key={key + i}
                        className={styles.cell}
                        style={
                          props.columnWidth && i <= props.columnWidth.length
                            ? { width: props.columnWidth[i] }
                            : {}
                        }
                      >
                        <div className={styles.item}>{item}</div>
                      </div>
                    ))}
                  </div>
                </tr>
              )}
              headerRowRenderer={({ className, columns, style }) => (
                <div className={className} role="row" style={style}>
                  <div className={styles.header}>
                    {columns.map((item, i) => (
                      <th
                        key={i}
                        className={styles.header_cell}
                        style={
                          props.columnWidth && i <= props.columnWidth.length
                            ? { width: props.columnWidth[i] }
                            : {}
                        }
                      >
                        <div className={styles.item}>{item}</div>
                      </th>
                    ))}
                  </div>
                </div>
              )}
              onScroll={({ clientHeight, scrollHeight, scrollTop }) => {
                if (
                  props.OnEndOfScroll &&
                  scrollHeight - clientHeight - scrollTop <= 10
                )
                  props.OnEndOfScroll();
              }}
            >
              {props.theads.map((m, index) => (
                <Column key={m + index} label={m} dataKey={index} width={100} />
              ))}
            </Table>
          );
        }}
      </AutoSizer>
    </div>
  );
}
