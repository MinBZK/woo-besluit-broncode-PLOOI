import { HeadingH4Atom } from '../../../ui/atoms';
import { CenteredLayout } from '../../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms/Headings',
    component: HeadingH4Atom,
    args: { 
        children: 'Heading H4'
    },
} as ComponentMeta<typeof HeadingH4Atom>

export const H4: ComponentStory<typeof HeadingH4Atom> = (args: any) => <CenteredLayout>
    <HeadingH4Atom {...args} />
</CenteredLayout>