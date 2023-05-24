import { DividerAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: DividerAtom,
    argTypes: {
        type: {
            options: ['default', 'primary'],
            control: { type: 'radio' }
        }
    },
    args: {
        verticaal: false,
        type: undefined
    }
} as ComponentMeta<typeof DividerAtom>

export const Divider: ComponentStory<typeof DividerAtom> = (args: any) => <CenteredLayout>
    <div style={{ height: 50, width: 50,}}>
        <DividerAtom {...args} />
    </div>
</CenteredLayout>