import { HeadingH3Atom } from '../../../ui/atoms';
import { CenteredLayout } from '../../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms/Headings',
    component: HeadingH3Atom,
    args: { 
        children: 'Heading H3'
    },
} as ComponentMeta<typeof HeadingH3Atom>

export const H3: ComponentStory<typeof HeadingH3Atom> = (args: any) => <CenteredLayout>
    <HeadingH3Atom {...args} />
</CenteredLayout>