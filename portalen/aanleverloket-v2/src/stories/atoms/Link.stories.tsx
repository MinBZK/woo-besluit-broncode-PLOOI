import { LinkAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: LinkAtom,
    args: { 
        href: "https://google.com",
        lang: "en",
        text: "Naar google",
        newWindow: true
    },
} as ComponentMeta<typeof LinkAtom>

export const Link: ComponentStory<typeof LinkAtom> = (args: any) => <CenteredLayout>
    <LinkAtom {...args} />
</CenteredLayout>