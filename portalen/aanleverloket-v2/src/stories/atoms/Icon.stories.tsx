import { IconAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: IconAtom,
    argTypes: { 
        icon: {
          options: ['icon-arrow-up', 'icon-calendar-blue', 'icon-explanation'],
          control: { type: 'radio' },
        }
    },
    args: { 
        alt: "icon",
        size: 'medium',
        icon: "icon-arrow-up"
    },
} as ComponentMeta<typeof IconAtom>

export const Icon: ComponentStory<typeof IconAtom> = (args: any) => <CenteredLayout>
    <IconAtom {...args} />
</CenteredLayout>