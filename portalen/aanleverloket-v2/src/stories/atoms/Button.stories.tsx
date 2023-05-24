import { CenteredLayout } from "../../ui/layouts";
import { ButtonAtom, LabelAtom } from "../../ui/atoms";
import { ComponentStory, ComponentMeta } from '@storybook/react';

export interface Args {
    type?: 'primary'
}

export default {
    title: "KOOP-React/Atoms",
    component: ButtonAtom,
    argTypes: {
        type: {
            options: ['default', 'primary',  "unstyled" , "orange" , "blue"],
            control: { type: 'radio' }
        }
    },
    args: {
        type: 'default',
        children: 'Verwijderen'
    }
} as ComponentMeta<typeof ButtonAtom>

export const Button: ComponentStory<typeof ButtonAtom> = (args: any) => <CenteredLayout>
    <ButtonAtom id="id" type={args.type} onClick={() => alert('Clicked!')}>
        {
            <LabelAtom type={args.type === 'primary' ? "blue" : "form"}>{args.children}</LabelAtom>
        }
    </ButtonAtom>
</CenteredLayout>