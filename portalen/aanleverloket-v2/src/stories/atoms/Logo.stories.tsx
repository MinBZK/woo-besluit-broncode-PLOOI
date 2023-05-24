import { LogoAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: LogoAtom,
    args: { 
        alt: "Logo",
        size: 'large',
        src: "https://open.overheid.nl/cb-common/2.0.0/images/logo.svg",
        className: ""
    },
} as ComponentMeta<typeof LogoAtom>

export const Logo: ComponentStory<typeof LogoAtom> = (args: any) => <CenteredLayout>
    <LogoAtom {...args} />
</CenteredLayout>