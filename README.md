# Title Zones
A Sponge plugin that allows you to define zones that send Titles to Players who cross their borders.
![Mt Everest](https://i.succ.in/WYVrG3XV.png)
![The Himalayas](https://i.succ.in/WYgsc2ri.png)
![The Grand Canyon](https://i.succ.in/WYfmq6oy.jpg)

# Example Config
[See this docs page for more examples of serialized Text](https://docs.spongepowered.org/stable/en/plugin/text/representations/configurate.html)
```
zones=[
    {
        position {
            x=0.0
            y=64.0
            z=0.0
        }
        radius=30
        subtitle {
            color=blue
	    bold=true
            text="extra information (subtitle)"
        }
        title {
            color=blue
            text="Zone 1"
        }
    },
    {
        position {
            x=100.0
            y=64.0
            z=100.0
        }
        radius=30
        subtitle {
            color=red
	    bold=true
            text="extra information (subtitle 2)"
        }
        title {
            color=red
            text="Zone 2"
        }
    },
    {
        position {
            x=6955.0
            y=253
            z=4919
        }
        radius=150
        subtitle {
            color=aqua
            text="Otherwise known as Peak XV"
        }
        title {
            color=aqua
            bold=true
            text="Mt Everest"
        }
    }
]
```

# Optional Dependencies
##### Plugins that give Title Zones extra functionality/features
[Boxboy](https://ore.spongepowered.org/Xemiru/Boxboy) - Enables an in-game menu - accessible with the permission 
`titlezones.menu` - that lists all named zones, their coordinates, and allows the user to teleport to them.