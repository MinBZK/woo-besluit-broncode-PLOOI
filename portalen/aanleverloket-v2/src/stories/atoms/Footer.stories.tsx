import { FooterAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: FooterAtom,
    args: {
        children: 'Footer'
    },
} as ComponentMeta<typeof FooterAtom>

export const Footer: ComponentStory<typeof FooterAtom> = (args: any) => <CenteredLayout>
    <div style={{ width: '100%' }}>
        <FooterAtom {...args} />
    </div>
</CenteredLayout>
