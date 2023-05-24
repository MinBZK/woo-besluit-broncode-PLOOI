import styles from './styles.module.scss';

interface Props {
    children:any;
    additionalClassname?:string;
}

export function FormAtom(props:Props){
   return <form className={`${styles.form} ${props.additionalClassname}`} >
       {
           props.children
       }
   </form>
}