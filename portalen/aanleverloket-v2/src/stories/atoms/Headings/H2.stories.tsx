import { HeadingH2Atom } from '../../../ui/atoms';
import { CenteredLayout } from '../../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms/Headings',
    component: HeadingH2Atom,
    args: { 
        children: 'Heading H2'
    },
} as ComponentMeta<typeof HeadingH2Atom>

export const H2: ComponentStory<typeof HeadingH2Atom> = (args: any) => <CenteredLayout>
    <HeadingH2Atom {...args} />
</CenteredLayout>