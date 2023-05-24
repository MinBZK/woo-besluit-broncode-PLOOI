import styles from './styles.module.scss';

interface Props {
    src: string;
    size: "icon" | "small" | "medium" | "large";
    alt: string;
    className?: string;
}

export function LogoAtom(props: Props) {
    let className;

    switch (props.size) {
        case 'icon':
            className = styles.logo__icon;
            break;
        case 'small':
            className = styles.logo__small;
            break;

        case 'medium':
            className = styles.logo__medium;
            break;

        case 'large':
            className = styles.logo__large;
            break;

    }
    
    className = props.className 
                ? `${className} ${props.className}`
                : className;

    return <img className={className} alt={props.alt} src={props.src} />
}