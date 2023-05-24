import styles from './styles.module.scss';

interface Props
{
    children: any;
    link?: boolean;
    bold?: boolean;
}

export function ListItemAtom(props:Props)
{
    let className = styles.list_item;
    
    if (props.link)
        className += ` ${styles.list_item__linked}`;
    
    if (props.bold)
        className += ` ${styles.list_item__bold}`;

    return <li className={className}>{props.children}</li>
}