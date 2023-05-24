import { ButtonAtom, IconAtom } from '../../atoms';

interface Props {
    id: string;
    onClick: () => any;
}

export function ButtonRemoveMolecule(props: Props) {
    return <ButtonAtom id={props.id} type='unstyled' onClick={props.onClick}>
        <IconAtom icon='icon-remove' alt='Verwijderen' size='medium' />
    </ButtonAtom>
}