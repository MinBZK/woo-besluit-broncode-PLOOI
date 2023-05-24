import { CenteredLayout } from "../../ui/layouts";
import { FoldableMolecule } from "../../ui/molecules";
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: "KOOP-React/Molecules",
    component: FoldableMolecule,
    args: {
        label: "label tekst"       
    }
} as ComponentMeta<typeof FoldableMolecule>

export const Foldable: ComponentStory<typeof FoldableMolecule> = (args: any) => <CenteredLayout>
    <div style={{ width: 500 }}>
        <FoldableMolecule {...args}><p>tekst</p></FoldableMolecule>
    </div>
</CenteredLayout>