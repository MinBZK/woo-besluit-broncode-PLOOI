import { ListMolecule } from "..";
import {
    FooterAtom,
    ContainerAtom
} from "../../atoms";
import { IExternalLink } from "../../interfaces/Link";

interface Props {
    menus: IExternalLink[][];
}

export function FooterMolecule(props: Props) {
    return <FooterAtom>
        <ContainerAtom centered type="row">
            {
               props.menus.map((menuItems, i) => <ListMolecule key={i} linked links={menuItems} />)
            }
        </ContainerAtom>
    </FooterAtom>
}