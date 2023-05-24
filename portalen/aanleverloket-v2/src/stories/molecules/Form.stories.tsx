import { ButtonMolecule, FormMolecule, TextInputMolecule } from '../../ui/molecules';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';
import { ContainerAtom, HeadingH1Atom, SpacerAtom } from '../../ui/atoms';

export default {
    title: 'KOOP-React/Molecules',
    component: FormMolecule,
    args: {
        rightButton: <ButtonMolecule id='rightButton' title='Button' onClick={() => { }} text={"Previous"} type={"default"} />,
        leftButton: <ButtonMolecule id='leftButton' title='Button' onClick={() => { }} text={"Complete"} type={"primary"} />
    },
} as ComponentMeta<typeof FormMolecule>

export const Formulier: ComponentStory<typeof FormMolecule> = (args: any) => <CenteredLayout>
    <ContainerAtom>
        <FormMolecule {...args}>
            <HeadingH1Atom>Document uploaden</HeadingH1Atom>
            <SpacerAtom space={8} />
            <TextInputMolecule id={"onderwerp"} label={"Onderwerp"} onEnter={() => { }} />
            <SpacerAtom space={4} />
            <TextInputMolecule id={"omschrijving"} label={"Omschrijving document"} onEnter={() => { }} />
            <SpacerAtom space={4} />
            <TextInputMolecule id={"titel"} label={"Officiële titel"} onEnter={() => { }} />
            <SpacerAtom space={4} />
            <TextInputMolecule id={"verkorte titel"} label={"Verkorte titel"} onEnter={() => { }} />
            <SpacerAtom space={4} />
        </FormMolecule>
    </ContainerAtom>
</CenteredLayout>