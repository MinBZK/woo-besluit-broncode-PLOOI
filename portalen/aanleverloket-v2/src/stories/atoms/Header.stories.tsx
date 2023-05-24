import { HeaderAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: HeaderAtom,
    args: {
        children: "Header Atom"
    },
} as ComponentMeta<typeof HeaderAtom>

export const Header: ComponentStory<typeof HeaderAtom> = (args: any) => <CenteredLayout>
    <div style={{width: '100%'}}>
        <HeaderAtom {...args} />
    </div>
</CenteredLayout>